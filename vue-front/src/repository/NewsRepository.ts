import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository.ts'
import News from '@/entity/News.ts'
import NewsResponse from '@/entity/NewsResponse.ts'

@singleton()
export default class NewsRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public getList(page: number, auth: string | null) {
    return this.httpRepository.getList(
      {
        method: 'GET',
        path: `news?page=${page}&size=10`,
        headers: {
          Authorization: auth,
        },
      },
      News,
    )
  }

  public get(newsId: Number | undefined, auth: string | null) {
    return this.httpRepository.get(
      {
        method: 'GET',
        path: `news/${newsId}`,
        headers: {
          Authorization: auth,
        },
      },
      NewsResponse,
    )
  }
}
