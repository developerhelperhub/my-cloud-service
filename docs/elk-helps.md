## search indices

http://localhost:9200/_cat/indices?v

http://localhost:5601/


https://hub.docker.com/_/elasticsearch


### filebeat reading logs from docker container

filebeat.inputs:
- type: docker
  containers.ids:
    - my-cloud-discovery
  containers.paths:
    - '/var/lib/docker/containers/my-cloud-discovery/*.log'

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
  indices:
    - index: "filebeat-%{[agent.version]}-%{+yyyy.MM.dd}"

### filebeat reading logs from container

filebeat.inputs:
- type: container
  paths: 
    - '/var/lib/docker/containers/*/*.log'

processors:
- add_docker_metadata:
    host: "unix:///var/run/docker.sock"

- decode_json_fields:
    fields: ["message"]
    target: "json"
    overwrite_keys: true

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
  indices:
    - index: "filebeat-%{[agent.version]}-%{+yyyy.MM.dd}"

logging.json: true
logging.metrics.enabled: false

