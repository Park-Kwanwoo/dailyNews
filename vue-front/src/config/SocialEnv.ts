export default class SocialEnv {
  readonly kakao_rest_api_key = import.meta.env.VITE_KAKAO_REST_API_KEY
  readonly redirect_uri = import.meta.env.VITE_REDIRECT_URI
  readonly naver_client_id = import.meta.env.VITE_NAVER_CLIENT_ID
}
