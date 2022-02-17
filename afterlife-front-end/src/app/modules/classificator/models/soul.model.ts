import { ClassifiedAfterworldSection } from '../models/classifiedAfterworldSection.model';

export interface Soul {
  id: number;
  firstName: string;
  lastName: string;
  dateOfDeath: Date;
  classifiedAfterworldSection: ClassifiedAfterworldSection | null;
}
