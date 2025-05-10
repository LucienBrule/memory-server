# QA Steps

1. Download the binaries

```shell
$ wget https://github.com/LucienBrule/memory-server/releases/download/v0.1.1/memory-cli-v0.1.1-linux-x86_64
$ wget https://github.com/LucienBrule/memory-server/releases/download/v0.1.1/memory-proxy-v0.1.1-linux-x86_64
```

2. Copy the binaries into the path

```shell
# ls
# memory-cli-v0.1.1-linux-x86_64  memory-proxy-v0.1.1-linux-x86_64

cp memory-proxy-v0.1.1-linux-x86_64 ~/.local/bin/memory-proxy

cp memory-cli-v0.1.1-linux-x86_64 ~/.local/bin/memory-cli
```

3. Make sure the binaries are executable

```shell
chmod +x ~/.local/bin/memory-proxy

chmod +x ~/.local/bin/memory-cli
```

4. Check configuration

```shell
cat ~/.memory/config.toml
endpoint = "https://memory-server-endpoint.tld"
apiKey = "memory-server-api-key"
profile = "prod"
```

5. Check / Edit Goose Config

```yaml
GOOSE_MODEL: anthropic/claude-3.5-sonnet
GOOSE_PROVIDER: openrouter
extensions:
  developer:
    bundled: true
    display_name: Developer
    enabled: true
    name: developer
    timeout: 300
    type: builtin
  memory-store:
    bundled: false
    display_name: MemoryStore
    enabled: true
    name: memory-store
    timeout: 300
    args: []
    cmd: memory-proxy
    type: stdio
```