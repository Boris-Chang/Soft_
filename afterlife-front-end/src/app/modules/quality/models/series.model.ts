import { Measurement } from '../models/measurement.model';
import { Values } from './values.model';

export interface Series {
    id: number;
    measurement: Measurement;
    name: string;
    values: Values;
  }
  