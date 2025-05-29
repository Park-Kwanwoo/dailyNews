import { inject, singleton } from 'tsyringe'
import HttpRepository from '@/repository/HttpRepository.ts'
import SubjectRequest from '@/request/SubjectRequest.ts'
import Subject from '@/entity/Subject.ts'

@singleton()
export default class SubjectRepository {
  constructor(@inject(HttpRepository) private readonly httpRepository: HttpRepository) {}

  public registerSubject(request: SubjectRequest, auth: string | null) {
    return this.httpRepository.post({
      path: '/api/register/subject',
      body: request,
      method: 'POST',
      headers: {
        Authorization: auth,
      },
    })
  }

  public getSubjects(auth: string | null) {
    return this.httpRepository
      .get<Subject>(
        {
          path: '/api/subject',
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
}
