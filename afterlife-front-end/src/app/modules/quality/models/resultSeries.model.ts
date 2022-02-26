import { Measurement } from "./measurement.model";
import { Values } from "./values.model";

export interface ResultSeries
{
    id: number;
    measurement: Measurement;
    name: string;
    values: Values[];

}