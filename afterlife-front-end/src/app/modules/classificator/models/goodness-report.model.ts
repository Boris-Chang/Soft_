import { GoodnessEvidence, Soul } from '.';

export interface GoodnessReport {
  id: number,
  soul: Soul,
  goodnessEvidences: GoodnessEvidence[],
  uploadedAt: Date,
}
