#!/bin/bash
# Corta e imprime los offsets. Para validar que es correcto tendrían que aparecer todos una unica vez (exceptuando las posiciónes en donde
# aparecen carácteres especiales \n \r y \t
cat IndexFile* | cut -f2 | awk '$1=$1' FS=","  OFS="\n" | sort -n
