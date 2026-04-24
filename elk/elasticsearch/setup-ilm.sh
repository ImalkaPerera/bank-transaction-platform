#!/bin/bash

# Wait for Elasticsearch to be up and healthy
echo "Waiting for Elasticsearch to be healthy..."
until curl -s http://elasticsearch:9200 >/dev/null; do
    sleep 5
done

echo "Elasticsearch is up. Applying ILM policy..."

# 1. Create ILM Policy: Keep only 1 day of data
curl -X PUT "http://elasticsearch:9200/_ilm/policy/bank-logs-policy" \
     -u "elastic:${ELASTIC_PASSWORD:-BankAdmin123}" \
     -H 'Content-Type: application/json' \
     -d '{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_age": "1d",
            "max_size": "5gb"
          }
        }
      },
      "delete": {
        "min_age": "1d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}'

echo -e "\nApplying Index Template..."

# 2. Create Index Template: Mappings, Settings, and ILM Link
curl -X PUT "http://elasticsearch:9200/_index_template/bank-logs" \
     -u "elastic:${ELASTIC_PASSWORD:-BankAdmin123}" \
     -H 'Content-Type: application/json' \
     -d '{
  "index_patterns": ["bank-logs*"],
  "data_stream": {},
  "priority": 600,
  "template": {
    "settings": {
      "index.lifecycle.name": "bank-logs-policy",
      "number_of_shards": 1,
      "number_of_replicas": 0,
      "refresh_interval": "5s",
      "mapping.ignore_malformed": true
    },
    "mappings": {
      "properties": {
        "@timestamp":    { "type": "date" },
        "level":         { "type": "keyword" },
        "service":       { "type": "keyword" },
        "traceId":       { "type": "keyword" },
        "env":           { "type": "keyword" },
        "message":       { "type": "text" },
        "logger":        { "type": "keyword" },
        "http": {
          "properties": {
            "method":         { "type": "keyword" },
            "path":           { "type": "keyword" },
            "status":         { "type": "integer" },
            "responseTimeMs": { "type": "long" },
            "clientIp":       { "type": "ip" }
          }
        },
        "error": {
          "properties": {
            "type":     { "type": "keyword" },
            "category": { "type": "keyword" },
            "severity": { "type": "keyword" },
            "message":  { "type": "text" },
            "stack":    { 
              "type": "text",
              "index": false
            }
          }
        }
      }
    }
  }
}'

echo -e "\nILM and Index Template Setup Complete!"
