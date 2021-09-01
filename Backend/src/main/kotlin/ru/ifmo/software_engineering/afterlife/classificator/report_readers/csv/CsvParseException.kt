package ru.ifmo.software_engineering.afterlife.classificator.report_readers.csv

class CsvParseException(message: String, rowNumber: Int, colNumber: Int) :
    Exception("Report read failed at cell $rowNumber, $colNumber: $message")
