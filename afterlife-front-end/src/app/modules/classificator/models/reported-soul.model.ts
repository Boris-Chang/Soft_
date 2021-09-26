import { GoodnessReport, SinsReport, Soul } from './';

export interface ReportedSoul {
  soul: Soul,
  sinsReport?: SinsReport,
  goodnessReport?: GoodnessReport,
}
