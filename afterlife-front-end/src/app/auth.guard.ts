import {
  ActivatedRouteSnapshot,
  CanActivate,
  CanActivateChild,
  Router,
  RouterStateSnapshot
} from "@angular/router";
import {Injectable} from "@angular/core";
import {AuthService} from "./shared/services/auth.service";

@Injectable()
export class AuthGuard
  implements CanActivate, CanActivateChild {
  constructor(private auth: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const isLogged = this.auth.isLoggedIn()
    if (!isLogged) {
      this.router.navigate(['/login'])
    }
    return this.auth.isLoggedIn()
  }

  canActivateChild(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    return this.canActivate(next, state)
  }
}
