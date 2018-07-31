package com.labrador.authservice

import org.junit.jupiter.api.BeforeAll

class AuthorizationTests extends AbstractTests {
    private static final String ADMIN_USERNAME = 'admin'
    private static final String ADMIN_PASSWORD = 'password'
    private static final String USER_USERNAME = 'user'
    private static final String USER_PASSWORD = 'password'
    private static final String ORG_USERNAME = 'org:user'
    private static final String ORG_PASSWORD = 'password'
    private static final String ACCESS_PERMIT_ALL_URL = '/test/accessPermitAll'
    private static final String ACCESS_PERMIT_ALL_MESSAGE = 'accessPermitAll'
    private static final String ACCESS_ONLY_ADMIN_ROLE_URL = '/test/accessOnlyAdminRole'
    private static final String ACCESS_ONLY_ADMIN_ROLE_MESSAGE = 'accessOnlyAdminRole'
    private static final String ACCESS_ONLY_USER_ROLE_URL = '/test/accessOnlyUserRole'
    private static final String ACCESS_ONLY_USER_ROLE_MESSAGE = 'accessOnlyUserRole'
    private static final String ACCESS_AUTHENTICATED_URL = '/test/accessAuthenticated'
    private static final String ACCESS_AUTHENTICATED_MESSAGE = 'accessAuthenticated'
    private static final String ACCESS_ONLY_USER_ROLE_AND_ORG_USER_URL = '/test/accessOnlyUserRoleAndOrgUser'
    private static final String ACCESS_ONLY_USER_ROLE_AND_ORG_USER_MESSAGE = 'accessOnlyUserRoleAndOrgUser'
    private static final String ACCESS_CLIENT_MUST_HAS_ADMIN_ROLE_URL = '/test/accessClientMustHasAdminRole'
    private static final String ACCESS_CLIENT_MUST_HAS_ADMIN_MESSAGE = 'accessClientMustHasAdminRole'
    private static final String ACCESS_CLIENT_MUST_HAS_READ_SCOPE_URL = '/test/accessClientMustHasReadScope'
    private static final String ACCESS_CLIENT_MUST_HAS_READ_SCOPE_MESSAGE = 'accessClientMustHasReadScope'

    private String accessToken



}
