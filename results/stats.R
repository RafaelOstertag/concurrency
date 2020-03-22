#!/usr/bin/env Rscript
library(ggplot2)

args <- commandArgs(trailingOnly = TRUE)

if (length(args) != 2) {
  stop("Need input and output file")
}

results <- read.csv(file = args[1])
dataframe <- data.frame(results)

p <- ggplot(dataframe) +
  geom_bar(aes(Test.Name, mean.ms.), stat = "identity") +
  geom_errorbar(aes(x = Test.Name, ymin = mean.ms. - stddev.ms., ymax = mean.ms. + stddev.ms.)) +
  xlab("Tests") +
  ylab("Average Time [ms]") +
  theme(axis.text.x = element_text(angle = -90, hjust = 0))

ggsave(args[2], plot = p, width = 210, height = 297, units = 'mm')