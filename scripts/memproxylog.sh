#!/bin/bash

# Log all stdin to memory-proxy
LOG_DIR="/tmp/memproxy"
mkdir -p "$LOG_DIR"

TIMESTAMP=$(date +%Y%m%d-%H%M%S)
IN_LOG="$LOG_DIR/in-$TIMESTAMP.log"
OUT_LOG="$LOG_DIR/out-$TIMESTAMP.log"

# Log STDOUT and STDERR from memory-proxy
exec > >(tee "$OUT_LOG") 2>&1
exec < <(tee "$IN_LOG")

# Execute memory-proxy with all I/O captured
exec memory-proxy