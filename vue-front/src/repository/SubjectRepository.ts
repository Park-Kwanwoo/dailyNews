import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository.ts'
import SubjectRequest from '@/request/SubjectRequest.ts'
import Subject from '@/entity/Subject.ts'
import { ElMessage } from 'element-plus'
import router from '@/router'

@singleton()
export default class SubjectRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public save(request: SubjectRequest, auth: string | null) {
    return this.httpRepository
      .post({
        path: '/subject',
        body: request,
        method: 'POST',
        headers: {
          Authorization: auth,
        },
      })
      .then((r) => {
        const statusCode = r.statusCode
        if (statusCode == 'SUCCESS') router.go(0)
      })
      .catch((e) => {
        ElMessage.error('주제가 등록되지 않았습니다. 다시 시도해주세요')
      })
  }

  public getSubjects(auth: string | null) {
    return this.httpRepository
      .get<Subject>(
        {
          path: '/subject',
          method: 'GET',
          headers: {
            Authorization: auth,
          },
        },
        Subject,
      )
      .then((r) => {
        return r
      })
  }

  public update(request: SubjectRequest, auth: string | null) {
    return this.httpRepository
      .patch({
        method: 'PATCH',
        path: '/subject',
        body: request,
        headers: {
          Authorization: auth,
        },
      })
      .then((r) => {
        const statusCode = r.statusCode
        if (statusCode == 'SUCCESS') router.go(0)
      })
      .catch((e) => {
        ElMessage.error('주제가 수정되지 않았습니다. 다시 시도해주세요')
      })
  }
}
