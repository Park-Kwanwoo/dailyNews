import HttpRepository from '@/repository/HttpRepository.ts'
import { inject, singleton } from 'tsyringe'
import NaverLoginParams from '@/request/NaverLoginParams.ts'
import KakaoLoginParams from '@/request/KakaoLoginParams.ts'

@singleton()
export default class SocialLoginRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public naverLogin(req: NaverLoginParams) {
    return this.httpRepository
      .login({
        path: '/api/login/naver',
        body: req,
        method: 'POST',
      })
      .then((r) => {
        return r
      })
      .catch((e) => {
        return e
      })
  }

  public kakaoLogin(req: KakaoLoginParams) {
    return this.httpRepository
      .login({
        path: '/api/login/kakao',
        body: req,
        method: 'POST',
      })
      .then((r) => {
        return r
      })
      .catch((e) => {
        return e
      })
  }
}
