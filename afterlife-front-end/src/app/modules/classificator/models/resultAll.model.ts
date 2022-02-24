import { result } from "./result.model";

export interface ResultAll
{
    pageNumber: number;
    results: result[];
    totalCount: number;
}