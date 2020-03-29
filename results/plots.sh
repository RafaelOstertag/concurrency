#!/bin/bash

for f in *.csv
do
  Rscript --vanilla stats.R "$f" "${f%%.csv}.pdf"
done