export interface PagedResult<T> {
  results: T[];
  totalCount: number;
  pageNumber: number;
}
