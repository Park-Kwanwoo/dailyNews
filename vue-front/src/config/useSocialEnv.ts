import SocialEnv from '@/config/SocialEnv.ts'

export function useSocialEnv() {
  const socialEnv = new SocialEnv()
  return { socialEnv }
}
