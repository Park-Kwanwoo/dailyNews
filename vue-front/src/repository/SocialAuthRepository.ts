import HttpRepository from '@/repository/HttpRepository.ts'
import { inject, singleton } from 'tsyringe'
import router from '@/router'
import { ElMessage } from 'element-plus'
import LoginParams from '@/request/LoginParams.ts'
import { useAuthStore } from '@/store/useAuthStore.ts'

@singleton()
export default class SocialAuthRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public login(req: LoginParams) {
    const auth = useAuthStore()

    return this.httpRepository
      .login({
        path: '/social/login',
        body: req,
        method: 'POST',
      })
      .then((r) => {
        const accessToken = r.headers['authorization']
        auth.setToken(accessToken)
        router.replace('/main')
      })
      .catch((e) => {
        ElMessage.error(e.message)
        router.replace('/')
      })
  }

  public logout() {
    const auth = useAuthStore()

    return this.httpRepository
      .post({
        path: '/social/logout',
        method: 'POST',
        headers: {
          Authorization: auth.getToken(),
        },
      })
      .then((r) => {
        const statusCode = r.statusCode
        if (statusCode == 'SUCCESS') {
          auth.logout()
          router.replace('/')
        }
      })
  }
}
