#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

echo "Cleaning obsolete pre-regularization files for the 1.4.0 development line..."
rm -rf src/main/java/dev/celerbi/easyfarmersdelightcompat/compat/jade
rm -f src/main/resources/data/easyfarmersdelightcompat/tags/item/cutter_logs.json
rm -f src/main/resources/data/easyfarmersdelightcompat/tags/items/cutter_logs.json
rm -f src/main/java/dev/celerbi/easyfarmersdelightcompat/command/FarmerFillCommand.java
echo "Obsolete pre-regularization files cleaned."
