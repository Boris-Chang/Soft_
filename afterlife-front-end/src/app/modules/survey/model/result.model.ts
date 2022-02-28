import { SurveyInfo } from "./surveyInfo.model";

export interface Result
{
    results: SurveyInfo[];
    totalCount: string;
    pageNumber: string;
}