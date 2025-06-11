import { container } from 'tsyringe'
import NewsRepository from '@/repository/NewsRepository.ts'
import SocialAuthRepository from '@/repository/SocialAuthRepository.ts'
import SubjectRepository from '@/repository/SubjectRepository.ts'

export const NEWS_REPOSITORY = container.resolve(NewsRepository)
export const SOCIAL_AUTH_REPOSITORY = container.resolve(SocialAuthRepository)
export const SUBJECT_REPOSITORY = container.resolve(SubjectRepository)
