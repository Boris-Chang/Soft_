import { Soul } from "./soul.model";
import { SinsReport } from "./sins-report.model";
import { GoodnessReport } from "./goodness-report.model";

export interface result{
    soul: Soul;
    sinsReport: SinsReport;
    goodnessReport: GoodnessReport;
    lastUpdate: String;
}