import HttpRepository from '@/repository/HttpRepository.ts'
import { inject, singleton } from 'tsyringe'
import router from '@/router'
import { ElMessage } from 'element-plus'
import LoginParams from '@/request/LoginParams.ts'

@singleton()
export default class SocialLoginRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public naverLogin(req: LoginParams) {
    return this.httpRepository
      .login({
        path: '/api/social/login',
        body: req,
        method: 'POST',
      })
      .then((r) => {
        router.replace('/')
      })
      .catch((e) => {
        ElMessage.error(e.message)
        router.replace('/login')
      })
  }

  public kakaoLogin(req: LoginParams) {
    return this.httpRepository
      .login({
        path: '/api/social/login',
        body: req,
        method: 'POST',
      })
      .then((r) => {
        router.replace('/')
      })
      .catch((e) => {
        ElMessage.error(e.message)
        router.replace('/login')
      })
  }
}
