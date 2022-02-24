import { Soul } from "./soul.model";
import { SinEvidence } from "./sin-evidence.model";

export interface SinsReport {
  id: number;
  soul: Soul;
  sins: SinEvidence[];
  uploadedAt: string;
}
