import HttpRepository from '@/repository/HttpRepository.ts'
import { inject, singleton } from 'tsyringe'

@singleton()
export default class SocialLoginRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public login(code: string) {
    return this.httpRepository
      .login({
        path: '/api/social/kakao',
        params: { code },
      })
      .then((r) => {
        return r
      })
      .catch((e) => {
        return e
      })
  }
}
