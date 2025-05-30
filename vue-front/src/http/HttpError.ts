import type {AxiosError} from "axios";

export default class HttpError {

  private readonly status: number
  private readonly message: string

  constructor(e: AxiosError) {
    this.status = e.response?.data.code ?? '500'
    this.message = e.response?.data.message ?? '상태가 좋지 않습니다.'
  }

  public getMessage(): string {
    return this.message
  }
}
