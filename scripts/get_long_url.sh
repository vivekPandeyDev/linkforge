#!/bin/bash

curl -X POST http://localhost:3000/api/v1/shorten \
  -H "Content-Type: application/json" \
  -d '{
        "longUrl": "https://example.com/very/long/path"
      }'
