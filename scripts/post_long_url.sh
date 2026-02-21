#!/bin/bash

KEY=$(uuidgen)

curl -X POST http://localhost:3000/api/v1/shorten \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: $KEY" \
  -d '{
        "longUrl": "https://www.google.com"
      }'