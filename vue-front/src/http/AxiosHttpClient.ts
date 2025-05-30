import type { AxiosError, AxiosInstance, AxiosResponse } from 'axios'
import axios from 'axios'
import HttpError from '@/http/HttpError.ts'
import { singleton } from 'tsyringe'

export type HttpRequestConfig = {
  method: 'GET' | 'POST' | 'PATCH' | 'DELETE'
  path: string
  params?: any
  body?: any
}

@singleton()
export default class AxiosHttpClient {
  private readonly client: AxiosInstance = axios.create({
    timeout: 300000,
    timeoutErrorMessage: '타임아웃',
  })

  public request(config: HttpRequestConfig) {
    return this.client
      .request({
        method: config.method,
        url: config.path,
        params: config.params,
        data: config.body,
      })
      .then((r: AxiosResponse) => {
        return r
      })
      .catch((e: AxiosError) => {
        return Promise.reject(new HttpError(e))
      })
  }
}
