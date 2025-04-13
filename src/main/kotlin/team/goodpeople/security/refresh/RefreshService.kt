package team.goodpeople.security.refresh

class RefreshService {

    /**
     * Redis에 저장될 Refresh Token
     * - 저장 기간은 JWT Constants를 참조
     * - TODO
     * Key는 Refresh Token 자체 또는 사용자 id 등과 조합하여 저장.
     * 혹은 Refresh Token을 Key, 사용자 id 등을 Value로 해서 검증해도 될 것 같다.
     * 반대로도 가능할 듯
     * */

    // TODO: Redis에 Refresh Token 저장


    // TODO: Redis에 저장된 Refresh Token 조회


    // TODO: Redis에 저장된 Refresh Token 삭제


}