# AA360 Toolbox

A comprehensive collection of Bot Commands for Automation Anywhere 360 (A360) that extends the platform's capabilities with 100+ specialized commands for data manipulation, file operations, string processing, and more.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Command Categories](#command-categories)
  - [Actions](#actions)
  - [Conditionals](#conditionals)
  - [Properties](#properties)
  - [Variables](#variables)
  - [Iterators](#iterators)
- [Building from Source](#building-from-source)
- [Development](#development)
- [Testing](#testing)
- [Installation](#installation)
- [Support](#support)

## Overview

AA360 Toolbox is a professional-grade Bot Command package designed to enhance Automation Anywhere 360 workflows with powerful data manipulation and processing capabilities. Built with Java 11 and following AA360 Bot Command Development Standards, this package provides type-safe, well-tested commands that seamlessly integrate with your automation workflows.

**Key Highlights:**
- 100+ bot commands across 10+ categories
- Comprehensive test coverage with TestNG
- Type-safe implementations with proper error handling
- Consistent annotation patterns and naming conventions
- Production-ready with extensive validation

## Features

### Core Capabilities

- **Date & Time Operations**: Advanced date calculations, conversions, and manipulations
- **Data Structure Manipulation**: Comprehensive operations for Lists, Dictionaries, Tables, and Records
- **File System Operations**: File handling, path utilities, and content management
- **String Processing**: Encoding, formatting, and transformation utilities
- **Number Operations**: Mathematical calculations and number utilities
- **URL Handling**: URL parsing, encoding, and query parameter extraction
- **Security**: TOTP generation for two-factor authentication
- **HTML Processing**: Table iteration and HTML manipulation

### Command Types

| Type | Count | Description |
|------|-------|-------------|
| **Actions** | 64 | Data transformation and computation commands |
| **Conditionals** | 13 | Boolean condition checks for workflow logic |
| **Properties** | 23 | Data property extraction and formatting |
| **Variables** | 9 | Predefined constant values |
| **Iterators** | 1 | Custom iteration capabilities |

## Command Categories

### Actions

Actions perform data transformations and computations, returning modified values.

<details>
<summary><b>Date/DateTime (10 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `CalculateDateTimeDuration` | Calculates duration between two datetime values |
| `ConvertDateTimeToEpoch` | Converts datetime to Unix epoch timestamp |
| `GetFirstDayOfMonth` | Gets the first day of the month for a given date |
| `GetFirstDayOfYear` | Gets the first day of the year for a given date |
| `GetLastDayOfMonth` | Gets the last day of the month for a given date |
| `GetLastDayOfYear` | Gets the last day of the year for a given date |
| `GetNextDayOfWeek` | Gets next occurrence of a specific day of the week |
| `GetNextWorkDate` | Gets the next working day (excludes weekends) |
| `GetPreviousDayOfWeek` | Gets previous occurrence of a specific day of the week |
| `GetPreviousWorkDate` | Gets the previous working day (excludes weekends) |

</details>

<details>
<summary><b>Dictionary (5 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `FindCommonKeys` | Finds keys that exist in both dictionaries |
| `FindUncommonKeys` | Finds keys unique to each dictionary |
| `GetKeysAsList` | Extracts all dictionary keys as a list |
| `GetValuesAsList` | Extracts all dictionary values as a list |
| `MergeDictionary` | Merges two dictionaries with conflict resolution |

</details>

<details>
<summary><b>File (8 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `CombineFilePath` | Safely combines file path components |
| `DecodeBase64ToFile` | Decodes Base64 string and writes to file |
| `EncodeFileToBase64` | Encodes file contents to Base64 string |
| `GetAvailableDirectory` | Finds available directory name with number suffix |
| `GetAvailableFile` | Finds available filename with number suffix |
| `GetFilesWithExtensions` | Lists files matching specified extensions |
| `GetSanitizedFileName` | Removes invalid characters from filename |
| `GetTextFromFile` | Reads file contents with encoding support |

</details>

<details>
<summary><b>List (11 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `AddItem` | Adds item to list at specified position |
| `ChunkList` | Splits list into smaller chunks of specified size |
| `DiffList` | Finds items in first list not in second list |
| `FindCommonItems` | Finds items present in both lists |
| `MergeList` | Combines two lists with duplicate handling |
| `RemoveDuplicatesList` | Removes duplicate items from list |
| `RemoveEmptyItemsList` | Removes empty/null items from list |
| `ReverseList` | Reverses the order of list items |
| `ShuffleList` | Randomly shuffles list items |
| `SortList` | Sorts list in ascending or descending order |
| `StripList` | Trims whitespace from all string items |

</details>

<details>
<summary><b>Number (6 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `CalculateSumSubset` | Calculates sum of numbers in list within range |
| `GetDecimalPart` | Extracts decimal portion of a number |
| `GetIntegerPart` | Extracts integer portion of a number |
| `GetMaxNumber` | Finds maximum number in a list |
| `GetMinNumber` | Finds minimum number in a list |
| `RoundNumber` | Rounds number to specified decimal places |

</details>

<details>
<summary><b>Record (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `SetRecord` | Creates or updates record with field values |
| `UpdateCell` | Updates a specific cell value in a record |

</details>

<details>
<summary><b>String (3 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `ChangeCase` | Converts text to uppercase, lowercase, or title case |
| `PadText` | Pads text with characters to specified length |
| `ReplaceTemplate` | Replaces placeholders in template with dictionary values |

</details>

<details>
<summary><b>Table (16 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `AddColumn` | Adds a new column to table with default values |
| `ConvertColumnToList` | Extracts table column as a list |
| `ConvertHeaderToList` | Extracts table headers as a list |
| `ConvertTableToDictionary` | Converts two-column table to dictionary |
| `ConvertTableToHTML` | Converts table to HTML table markup |
| `NormalizeHeaders` | Normalizes table headers (removes special chars, etc.) |
| `RemoveEmptyColumns` | Removes columns where all cells are empty |
| `RemoveEmptyRows` | Removes rows where all cells are empty |
| `ReverseTableRows` | Reverses the order of table rows |
| `SetRowAsHeader` | Promotes a data row to become table headers |
| `SetTableSchema` | Sets or updates table schema/headers |
| `SliceColumns` | Extracts subset of columns from table |
| `SliceTable` | Extracts subset of rows from table |
| `TrimHeaders` | Trims whitespace from all header names |
| `UpdateCell` | Updates a specific cell value in table |

</details>

<details>
<summary><b>URL (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `BuildURL` | Constructs URL from components with proper encoding |
| `GetQueryParameters` | Parses URL query parameters to dictionary |

</details>

<details>
<summary><b>Regex (1 command)</b></summary>

| Command | Description |
|---------|-------------|
| `MatchRegex` | Matches text against regex pattern with group extraction |

</details>

<details>
<summary><b>Security (1 command)</b></summary>

| Command | Description |
|---------|-------------|
| `GenerateTOTP` | Generates Time-based One-Time Password (TOTP) |

</details>

### Conditionals

Conditionals check conditions and return boolean values for workflow branching.

<details>
<summary><b>File Conditionals (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `HasFileWithExtension` | Checks if directory contains files with extension |
| `IsFileWritable` | Checks if file is writable |

</details>

<details>
<summary><b>Record Conditionals (5 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `HasRecordHeader` | Checks if record has specified header |
| `IsRecordBlank` | Checks if all cell values are empty or whitespace-only |
| `IsRecordEmpty` | Checks if all cell values are empty/null |
| `IsRecordSchemaBlank` | Checks if all schema headers are blank |
| `IsRecordSchemaEmpty` | Checks if record has no schema headers |

</details>

<details>
<summary><b>String Conditionals (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `StringBeginsWith` | Checks if string starts with prefix |
| `StringEndsWith` | Checks if string ends with suffix |

</details>

<details>
<summary><b>Table Conditionals (4 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `HasTableColumns` | Checks if table has all specified columns |
| `HasTableHeader` | Checks if table has a header schema |
| `IsTableSchemaBlank` | Checks if all table headers are blank |
| `IsTableSchemaEmpty` | Checks if table has no headers |

</details>

### Properties

Properties extract or transform data properties, typically used for data formatting.

<details>
<summary><b>CSV Properties (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `EscapeCSV` | Escapes string for CSV format |
| `UnescapeCSV` | Unescapes CSV-formatted string |

</details>

<details>
<summary><b>File Properties (4 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `GetFileExtension` | Extracts file extension from path |
| `GetFileNameWithExtension` | Extracts filename with extension from path |
| `GetFileNameWithoutExtension` | Extracts filename without extension from path |
| `GetParentFile` | Gets parent directory from file path |

</details>

<details>
<summary><b>HTML Properties (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `EscapeHTML` | Escapes string for HTML entity encoding |
| `UnescapeHTML` | Unescapes HTML entities to plain text |

</details>

<details>
<summary><b>JSON Properties (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `EscapeJSON` | Escapes string for JSON format |
| `UnescapeJSON` | Unescapes JSON-encoded string |

</details>

<details>
<summary><b>String Properties (7 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `Base64Decode` | Decodes Base64 string to plain text |
| `Base64Encode` | Encodes string to Base64 format |
| `NormalizeSpace` | Normalizes whitespace in string |
| `Strip` | Removes leading and trailing whitespace |
| `StripLeading` | Removes leading whitespace |
| `StripLeadingZeroes` | Removes leading zeros from numeric string |
| `StripTrailing` | Removes trailing whitespace |

</details>

<details>
<summary><b>URL Properties (4 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `DecodeURL` | Decodes URL-encoded string |
| `EncodeURL` | Encodes string for URL format |
| `GetHostname` | Extracts hostname from URL |
| `GetProtocol` | Extracts protocol from URL |

</details>

<details>
<summary><b>XML Properties (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `EscapeXML` | Escapes string for XML entity encoding |
| `UnescapeXML` | Unescapes XML entities to plain text |

</details>

### Variables

Variables provide constant or computed values for common use cases.

<details>
<summary><b>File Variables (2 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `UserDownloadDirectory` | Returns user's Downloads directory path |
| `UserHomeDirectory` | Returns user's home directory path |

</details>

<details>
<summary><b>Record Variables (1 command)</b></summary>

| Command | Description |
|---------|-------------|
| `EmptyRecord` | Provides an empty record value |

</details>

<details>
<summary><b>String Variables (4 commands)</b></summary>

| Command | Description |
|---------|-------------|
| `AuthToken` | Retrieves authentication token from environment |
| `EmptyString` | Provides an empty string value |
| `UUID` | Generates a new UUID |
| `Username` | Retrieves current username |

</details>

<details>
<summary><b>Table Variables (1 command)</b></summary>

| Command | Description |
|---------|-------------|
| `EmptyTable` | Provides an empty table value |

</details>

<details>
<summary><b>URL Variables (1 command)</b></summary>

| Command | Description |
|---------|-------------|
| `CRURL` | Provides Control Room URL |

</details>

### Iterators

<details>
<summary><b>HTML Iterator (1 command)</b></summary>

| Command | Description |
|---------|-------------|
| `HTMLTableIterator` | Iterates through HTML table rows and cells |

</details>

## Building from Source

### Prerequisites

- Java JDK 11
- Gradle 7.x or higher
- AA360 Bot Command SDK (included in `libs/` directory)

### Build Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/A360-Tools/Toolbox.git
   cd Toolbox
   ```

2. **Build the Package**
   ```bash
   # Windows
   gradlew.bat clean shadowJar

   # Linux/Mac
   ./gradlew clean shadowJar
   ```

3. **Locate the Package**

   The compiled package will be in:
   ```
   build/libs/Toolbox.jar
   ```

4. **Upload to Control Room**

   Follow the [Installation](#installation) instructions to upload the built package.

## Development

### Project Structure

```
Toolbox/
├── src/
│   ├── main/
│   │   ├── java/sumit/devtools/
│   │   │   ├── actions/          # Action commands
│   │   │   ├── conditionals/     # Conditional commands
│   │   │   ├── properties/       # Property commands
│   │   │   ├── variables/        # Variable commands
│   │   │   ├── iterators/        # Iterator commands
│   │   │   └── utils/            # Utility classes
│   │   └── resources/
│   │       ├── icons/            # Command icons
│   │       ├── locales/          # Localization files
│   │       └── package.template  # Package metadata template
│   └── test/
│       └── java/                 # Test classes
├── libs/                         # AA360 SDK libraries
├── build.gradle                  # Gradle build configuration
├── CLAUDE.md                     # Development standards
└── README.md                     # This file
```

### Coding Standards

This project follows strict AA360 Bot Command Development Standards documented in `CLAUDE.md`. Key standards include:

- **Return Label Pattern**: Use "Assign \<specific_output\> to"
- **Boolean Inputs**: Use `AttributeType.BOOLEAN` with explicit options
- **Class Naming**:
  - Actions: Verb-based (e.g., `GetMaxNumber`, `ConvertTableToHTML`)
  - Conditionals: State checks (`Is...`, `Has...`, `Are...`)
  - Properties: Direct verb form (e.g., `Base64Encode`, `EscapeHTML`)
  - Variables: Noun forms only (e.g., `EmptyString`, `UUID`)
- **Annotation Guidelines**: Proper capitalization, tense, and structure
- **Parameter Ordering**: HELP parameters must always be last

### Adding New Commands

1. Create command class in appropriate package
2. Follow naming conventions from `CLAUDE.md`
3. Add proper annotations (`@BotCommand`, `@CommandPkg`, etc.)
4. Implement command logic with error handling
5. Create corresponding test class
6. Update this README with command documentation

## Testing

### Running Tests

```bash
# Run all tests
gradlew test

# Run specific test class
gradlew test --tests "date.GetNextDayOfWeekTest"

# Run tests with detailed output
gradlew test --info
```

### Test Coverage

The project includes comprehensive test coverage:
- Unit tests for all commands
- Edge case validation
- Error handling verification
- Type safety checks

### Test Structure

Tests use TestNG framework and follow consistent patterns:
- `@BeforeMethod`: Set up test data
- `@Test`: Individual test cases
- `@AfterMethod`: Clean up resources


## Installation

### How to Install

We provide two package formats for your convenience:

#### **Option 1: Jar File (Recommended)**

1. **Download the Package**
    - Download the latest `.jar` file from the [releases page](https://github.com/A360-Tools/Toolbox/releases)

2. **Import to Control Room**
    - Log in to your A360 Control Room
    - Navigate to **Manage** → **Packages**
    - Click **Import** or **Upload package**
    - Select the downloaded `.jar` file
    - Click **Upload**

#### **Option 2: Zip File (Community Edition Compatible)**

1. **Download the Package**
    - Download the latest `.zip` file from the [releases page](https://github.com/A360-Tools/Toolbox/releases)

2. **Import to Control Room**
    - Log in to your A360 Control Room
    - Navigate to **Automation** → **Import**
    - Select the downloaded `.zip` file
    - Complete the import process

### Using the Package in Your Bots

Once installed:
1. Open Bot Editor
2. In the Actions panel, search for **"Toolbox"** or specific command names
3. Drag and drop commands into your bot workflow

### Important Note for Community Edition Control Room Users

> **Warning**: Please be aware that in the Community Edition Control Room, the system does not automatically set the latest version of imported packages as the default. To ensure you are using the latest and fully functional features, you must manually change the package version to the latest version in every bot.

**To update package version in your bots:**
1. Open your bot in Bot Editor
2. Click on any Toolbox command in your workflow
3. In the properties panel, locate the package version dropdown
4. Select the latest version from the dropdown
5. Save your bot

## Support

For issues, questions, or contributions:
- **Repository**: [A360-Tools/Toolbox](https://github.com/A360-Tools/Toolbox)
- **Issue Tracker**: [GitHub Issues](https://github.com/A360-Tools/Toolbox/issues)
- **Documentation**: See `CLAUDE.md` for development standards
- **Author**: Sumit Kumar

---

**Made with care for the AA360 automation community**
