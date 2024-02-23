package jpabook.dashdine.domain.user;

public enum UserRoleEnum {

    CUSTOMER(Authority.CUSTOMER),  // 고객
    OWNER(Authority.OWNER);  // 사장님

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
    }
}
