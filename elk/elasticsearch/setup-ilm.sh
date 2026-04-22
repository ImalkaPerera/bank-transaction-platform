#!/bin/bash

# Wait for Elasticsearch to be up and healthy
echo "Waiting for Elasticsearch to be healthy..."
until curl -s http://elasticsearch:9200 >/dev/null; do
    sleep 5
done

echo "Elasticsearch is up. Applying ILM policy..."

# Create ILM Policy: Delete after 1 day
curl -X PUT "http://elasticsearch:9200/_ilm/policy/bank_logs_retention_policy" \
     -u "elastic:${ELASTIC_PASSWORD}" \
     -H 'Content-Type: application/json' \
     -d '{
  "policy": {
    "phases": {
      "hot": {
        "actions": {}
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

# Create Index Template: Apply policy to bank-logs-*
curl -X PUT "http://elasticsearch:9200/_index_template/bank_logs_template" \
     -u "elastic:${ELASTIC_PASSWORD}" \
     -H 'Content-Type: application/json' \
     -d '{
  "index_patterns": ["bank-logs-*"],
  "template": {
    "settings": {
      "index.lifecycle.name": "bank_logs_retention_policy"
    }
  }
}'

echo -e "\nILM Setup Complete!"
