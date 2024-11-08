# Mortgage Match

A JavaFX-based mortgage calculator application that helps users calculate mortgage payments and determine an affordable home price based on various financial inputs. 
The application also includes features like amortization schedules and affordability calculators with graphical visualizations.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)

---

## Features
- **Mortgage Payment Calculator**: Calculate monthly mortgage payments based on loan amount, interest rate, and term.
- **Amortization Schedule**: View a breakdown of principal and interest payments over time.
- **Affordability Calculator**: Determine an affordable home price based on income, expenses, debt, and loan details.
- **Graphical Visualizations**: Display affordability data in bar charts and pie charts.
- **Export to PDF**: Download amortization schedules as a PDF for offline use.

## Installation

### Prerequisites
- **Java** 1.8 or later
- **JavaFX** SDK 23 or later
- **Maven** for project dependencies and build management

### Steps
1. **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/MortgageCalculator.git
    cd MortgageCalculator
    ```

2. **Build the project** using Maven:
    ```bash
    mvn clean package
    ```

3. **Run the application**:
    ```bash
    mvn javafx:run
    ```

## Usage

1. Open the application.
2. Use the **Home Calculator** section to input income, expenses, and loan details to determine an affordable price range.
3. Use the **Mortgage Calculator** section to calculate monthly mortgage payments.
4. View an **Amortization Schedule** that details principal and interest payments over the life of the loan.
5. Save the amortization schedule as a PDF if desired.

### Controls
- **Calculate**: Button to compute affordability or mortgage payment.
- **Clear**: Reset the form fields.
- **Exit**: Close the application.
- **View Amortization Schedule**: Open a detailed amortization schedule.

## Technologies Used

- **Java**: Core programming language.
- **JavaFX**: User interface toolkit for building the GUI.
- **Maven**: Build and dependency management.
- **iText**: For generating PDFs.
- **Apache Commons and JSON Libraries**: For various utilities and JSON handling.

