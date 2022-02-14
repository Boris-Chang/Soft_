import { User } from "./user.model";

export interface UserInfo {
    identity: User,

    roles: string
}