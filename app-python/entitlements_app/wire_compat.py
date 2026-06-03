"""Wire-format compatibility helpers -- keep JSON output byte-identical to the
Java Spring stack so HTTP clients are stack-agnostic.

Pydantic v2 defaults that the app must override for Java parity:
- ``decimal.Decimal`` is serialized as a JSON string by default; Jackson serializes
  ``BigDecimal`` as a JSON number. ``JacksonDecimal`` aligns Python with Jackson by
  emitting JSON number (via ``float()`` -- accepts the same precision tradeoff
  Jackson does).
- ``datetime.datetime`` is serialized with timezone offset (``2023-01-15T10:30:00+00:00``)
  by default; Jackson serializes ``Instant`` with a ``Z`` suffix
  (``2023-01-15T10:30:00Z``). ``JacksonDatetime`` aligns by replacing the trailing
  ``+00:00`` with ``Z`` for UTC instants.

"""

from __future__ import annotations

from datetime import datetime
from decimal import Decimal
from typing import Annotated

from pydantic import PlainSerializer


def _decimal_to_jackson_number(value: Decimal) -> float:
    """Serialize Decimal as a JSON number to match Jackson BigDecimal defaults."""
    return float(value)


def _datetime_to_jackson_instant(value: datetime) -> str:
    """Serialize datetime as ISO-8601 with a ``Z`` suffix to match Jackson Instant."""
    iso = value.isoformat()
    # Jackson emits `Z` for UTC; Pydantic emits `+00:00`. Aligning here means a
    # client that does string-equality on timestamps gets the same value from both
    # stacks. Non-UTC offsets pass through unchanged (still ISO-8601).
    if iso.endswith("+00:00"):
        return iso[:-6] + "Z"
    return iso


JacksonDecimal = Annotated[
    Decimal,
    PlainSerializer(_decimal_to_jackson_number, return_type=float, when_used="json"),
]
"""Decimal alias whose JSON serialization matches Jackson's BigDecimal default.

Use this in any Pydantic model that crosses an HTTP boundary; Python-internal code
still sees a real ``decimal.Decimal`` for arithmetic precision.
"""


JacksonDatetime = Annotated[
    datetime,
    PlainSerializer(_datetime_to_jackson_instant, return_type=str, when_used="json"),
]
"""datetime alias whose JSON serialization matches Jackson's Instant default."""