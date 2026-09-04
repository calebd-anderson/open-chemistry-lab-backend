# [FastAPI](https://fastapi.tiangolo.com/) Worker Service

## Development
- The build system is [uv](https://docs.astral.sh/uv/).
- Add dev dependency:
```sh
uv add --dev httpx2
```

### Test
```sh
uv run pytest
```

## CI
```sh
uv lock --check
uv run ruff check .
uv run ruff format --check .
uv run pytest
```