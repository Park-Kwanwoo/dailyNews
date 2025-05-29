import type NewsItems from '@/entity/NewsItems.ts'

export default class NewsResponse {
  public id = 0
  public title = ''
  public items: NewsItems[] = []
}
