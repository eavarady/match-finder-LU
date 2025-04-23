# Match Finder

This repository contains **Match Finder**, a Java tool designed to analyze SQLite databases and identify relational dependencies. It determines whether relations `x`, `y`, and `z` adhere to specific constraints, such as being derived through union or Cartesian product. If valid matches are found, the tool restores the original database by removing extraneous relations; otherwise, it outputs `"NO MATCH"`.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [File Structure](#file-structure)
- [Acknowledgments](#acknowledgments)

## Overview
This project focuses on relational database analysis by:
1. Identifying relations `x`, `y`, and `z` in a given SQLite database.
2. Validating if `z` was created from `x` and `y` through a **union** or **Cartesian product**.
3. Restoring the original database by removing unnecessary relations.
4. Outputting `"NO MATCH"` if no valid relationships are found.

## Features
- **Relational Analysis**: Detects and validates relational dependencies.
- **Restoration**: Reconstructs the original database by eliminating extraneous relations.
- **Error Handling**: Provides descriptive outputs, including `"NO MATCH"` for invalid cases.
- **SQLite Compatibility**: Works seamlessly with SQLite databases.

## Requirements
Ensure you have the following installed:
- Java Development Kit (JDK) 8 or higher
- SQLite (for database management)

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/eavarady/match-finder-LU.git
   cd match-finder-LU
