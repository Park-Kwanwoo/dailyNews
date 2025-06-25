import { inject, singleton } from 'tsyringe'
import type { HttpRequestConfig } from '@/http/AxiosHttpClient.ts'
import AxiosHttpClient from '@/http/AxiosHttpClient.ts'
import ApiResponse from '@/response/ApiResponse.ts'
import { plainToInstance } from 'class-transformer'
import Paging from '@/entity/Paging.ts'

@singleton()
export default class HttpRepository {
  constructor(@inject(AxiosHttpClient) private readonly httpClient: AxiosHttpClient) {}

  public login(config: HttpRequestConfig) {
    return this.httpClient
      .request({ ...config })
      .then((r) => {
        return r
      })
      .catch((e) => {
        return e
      })
  }

  public get<T>(config: HttpRequestConfig, clazz: { new (...args: any[]): T }): Promise<T> {
    return this.httpClient
      .request({ ...config })
      .then((r) => {
        const apiResponse = plainToInstance<ApiResponse<T>, any>(ApiResponse, r.data)
        const statusCode = apiResponse.statusCode

        if (statusCode === 'SUCCESS') {
          return plainToInstance(clazz, apiResponse.data)
        } else {
          return apiResponse
        }
      })
      .catch((e) => {
        return e
      })
  }

  public getList<T>(
    config: HttpRequestConfig,
    clazz: { new (...args: any[]): T },
  ): Promise<Paging<T>> {
    return this.httpClient.request({ ...config }).then((r) => {
      const apiResponse = plainToInstance<ApiResponse<Paging<T>>, any>(ApiResponse, r.data)
      const paging = plainToInstance<Paging<T>, any>(Paging, apiResponse.data)
      const items = plainToInstance<T, any>(clazz, paging.items)

      paging.setItems(items)
      return paging
    })
  }

  public post<T>(
    config: HttpRequestConfig,
    clazz: { new (...args: any[]): T } | null = null,
  ): Promise<ApiResponse<T>> {
    return this.httpClient
      .request({ ...config })
      .then((r) => {
        const apiResponse = plainToInstance<ApiResponse<T>, any>(ApiResponse, r.data)
        return apiResponse
      })
      .catch((e) => {
        return e
      })
  }

  public patch<T>(
    config: HttpRequestConfig,
    clazz: { new (...args: any[]): T } | null = null,
  ): Promise<ApiResponse<T>> {
    return this.httpClient
      .request({ ...config })
      .then((r) => {
        const apiResponse = plainToInstance<ApiResponse<T>, any>(ApiResponse, r.data)
        return apiResponse
      })
      .catch((e) => {
        return e
      })
  }
}
