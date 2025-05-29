import type { AxiosError, AxiosInstance, AxiosRequestHeaders, AxiosResponse } from 'axios'
import axios from 'axios'
import HttpError from '@/http/HttpError.ts'
import { singleton } from 'tsyringe'
import { useAuthStore } from '@/store/useAuthStore.ts'

export type HttpRequestConfig = {
  method: 'GET' | 'POST' | 'PATCH' | 'DELETE'
  path: string
  params?: any
  body?: any
  headers?: {
    Authorization?: string | null
  }
}

@singleton()
export default class AxiosHttpClient {
  private readonly client: AxiosInstance = axios.create({
    timeout: 300000,
    timeoutErrorMessage: '타임아웃',
    withCredentials: true,
  })

  constructor() {
    this.setInterceptor()
  }

  public request(config: HttpRequestConfig) {
    return this.client
      .request({
        method: config.method,
        url: config.path,
        params: config.params,
        data: config.body,
        headers: config.headers,
      })
      .then((r: AxiosResponse) => {
        return r
      })
      .catch((e: AxiosError) => {
        return Promise.reject(new HttpError(e))
      })
  }

  private setInterceptor() {
    this.client.interceptors.response.use((r: AxiosResponse) => {
      if (r.data.message == 'expired_accessToken') {
        this.reissueToken()
      }
      return r
    })
  }

  private reissueToken() {
    const auth = useAuthStore()

    this.request({
      path: '/api/token/reissue',
      method: 'GET',
    }).then((r) => {
      const accessToken = r.headers['authorization']
      auth.setToken(accessToken)
      return r
    })
  }
}
