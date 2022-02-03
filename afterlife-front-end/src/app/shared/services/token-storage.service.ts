import { Injectable } from '@angular/core';

const TOKEN_KEY = 'AuthToken';
const LOGIN_KEY = 'AuthLogin';
const AUTHORITIES_KEY = 'AuthAuthorities';

@Injectable({
    providedIn: 'root'
})

export class TokenStorageService {
    private roles: Array<string> = [];

    
    public saveToken(token: string) {
        window.sessionStorage.removeItem(TOKEN_KEY);
        window.sessionStorage.setItem(TOKEN_KEY, token);
    }
    public getToken(): string | null {
        return window.sessionStorage.getItem(TOKEN_KEY);
    }

    public saveLogin(login: string) {
        window.sessionStorage.removeItem(LOGIN_KEY);
        window.sessionStorage.setItem(LOGIN_KEY, login);
    }

    public saveAuthorities(authorities: string[])
    {
        window.sessionStorage.removeItem(AUTHORITIES_KEY);
        window.sessionStorage.setItem(AUTHORITIES_KEY, JSON.stringify(authorities));
    }

    public getAuthorities(): string[] {
        this.roles = [];
        if (sessionStorage.getItem(TOKEN_KEY))
        {
            JSON.parse(sessionStorage.getItem(AUTHORITIES_KEY)).forEach(authority => {
                this.roles.push(authority.authority);
            });
        }

        return this.roles;
    }
}
