import { inject, singleton } from 'tsyringe'
import type { HttpRequestConfig } from '@/http/AxiosHttpClient.ts'
import AxiosHttpClient from '@/http/AxiosHttpClient.ts'

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
}
