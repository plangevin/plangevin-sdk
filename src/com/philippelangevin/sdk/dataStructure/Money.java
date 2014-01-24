package com.philippelangevin.sdk.dataStructure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collection;

/**
 * <p> Title: {@link Money} <p>
 * <p> Description:  The purpose of this class is to calculate
 *                   correctly all mathematical operation on
 *                   money. This is required since operations
 *                   on Double or Float will always generates
 *                   rounding error in the context of monetary
 *                   calculation</p>
 * 
 * <p> Notes :  <li> This class is immutable.
 * 				<li> This class define its own NumberFormat as the static
 * 				     field FORMATTER.
 * </p>
 * <p> See :
 * 			<li> http://www.javapractices.com/topic/TopicAction.do?Id=13
 * 			<li> http://www.javapractices.com/topic/TopicAction.do?Id=213
 * 			<li> http://www.ibm.com/developerworks/java/library/j-jtp0114/
 * 			<li> http://download.oracle.com/javase/7/docs/api/index.html?overview-summary.html
 * 			<li> http://www.idinews.com/sourcecode/MoneyJava.html
 * <p> Company : C-Tec <p>
 *
 * @author Jonathan Giroux (jgiroux) jgiroux@ctecworld.com
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */
/*
 * History
 * ------------------------------------------------
 * Date        Name        BT      Description
 * 2010-09-24  jgiroux			   Initial definition
 * 2010-10-04  pcharette		   New formatter, more comments, now really immutable (final value).
 */

public class Money extends Number implements Comparable<Money>{

	private static final long serialVersionUID = -7007474303083425870L;
	
	//If thousand separator needed the DecimalFormat argument would be("###,###,###,##0.00")
	private static final String STRING_FORMAT = "0.00";
	private static final String DISPLAY_FORMAT = "0.00 $";

	//The special scale factor for division(to ensure proper significant decimal
	private static final int SCALE = 10;
	
	/**
	 *  Validate that a string can be formatted into the
	 *  monetary format using the local decimal symbol.
	 *  Parses the string to make a strict verification
	 *  that it can be formatted to the NumberFormat.
	 *  The dot is always a valid decimal symbol for this
	 *  class but the symbol use to format is the local one.
	 */
	public static final MoneyFormat FORMATTER = new MoneyFormat(STRING_FORMAT);
	public static final MoneyFormat DISPLAY_FORMATTER = new MoneyFormat(DISPLAY_FORMAT);
	
	public static class MoneyFormat extends NumberFormat {
		private static final long serialVersionUID = -6381375772053873001L;
		
		/**
		 * This DecimalFormat is use to parse and format the data. The problem is that DecimalFormat
		 * declare final some of the methods needed for a good MoneyFormat implementation but we
		 * don't want to rewrite this class.
		 */
		private final DecimalFormat VALUE_FORMATTER;
		
		public MoneyFormat(String format) {
			VALUE_FORMATTER = new DecimalFormat(format);
			
		}
		
		/* (non-Javadoc)
		 * @see java.text.NumberFormat#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
		 */
		@Override
		public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
			//default implementation throws an IllegalArgumentException if number is not of
			//a known type (and Money is unknown from the api)
			if (number instanceof Money) {
				return super.format(((Money)number).value, toAppendTo, pos);
			}
			return super.format(number, toAppendTo, pos);
		}
		
		@Override
		public Object parseObject(String source) throws ParseException {
			return VALUE_FORMATTER.parseObject(source);
		}
		
		@Override
		public Number parse(String source) throws ParseException {
			return VALUE_FORMATTER.parse(source);
		}

		@Override
		public Number parse(String source, ParsePosition parsePosition) {
			return VALUE_FORMATTER.parse(source, parsePosition);
		}
		
		@Override
		public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
			return VALUE_FORMATTER.format(number, toAppendTo, pos);
		}
		
		@Override
		public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			return VALUE_FORMATTER.format(number, toAppendTo, pos);
		}
	};
	
	private static final RoundingMode roundingMode = RoundingMode.HALF_UP;
	
	/** The Money object for the amount 0.00. */
	public static final Money ZERO = new Money(0);
	
	private final BigDecimal value;

	/**
	 * Copy constructor.
	 * @param m The Money to copy from.
	 * @throws NullPointerException If m is <code>null</code>.
	 */
	public Money(Money m) throws NullPointerException {
		value = m.value;
	}
	
	/**
	 * Creates a Money with the value m.
	 * @param m The value of this Money.
	 * @throws NullPointerException If m is <code>null</code>.
	 */
	public Money(Number m) throws NullPointerException {
		value = new BigDecimal(m.toString());
	}
	
	/**
	 * Creates a Money with the value of value.
	 * @param value The value of this Money.
	 * @throws NullPointerException If value is <code>null</code>.
	 */
	public Money(BigDecimal value) throws NullPointerException {
		if (value == null) {
			throw new NullPointerException("value can't be null");
		}
		this.value = value;
	}
	
	/**  <p>Title: {@link Money#Money(String s)}
	 *   <p>Description : Initialized the Money value to the value
	 *                    represented by the specified String. The
	 *                    decimal separator must be [.].
	 * @param s : a string representing a monetary value.
	 * @throws NumberFormatException
	 */
	public Money(String s) throws NumberFormatException, NullPointerException {
		value = new BigDecimal(s);
	}
	//END CONSTRUCTORS
	
	/**  <p>Title: {@link Money#parseMoney}
	 *   <p>Description : Returns a new Money object initialized
	 *                    to the value represented by the specified String.
	 *                    The string can be written with a [.] or with
	 *                    the decimal separator of the local.
	 *   @param  s : a string representing a monetary value.
	 *   @throws NumberFormatException
	 *   @return a Money object
	 */
	public static Money parseMoney(String s) throws NumberFormatException {
		try {
			return new Money(s);
		} catch (NumberFormatException e) {
			try {
				return new Money(FORMATTER.parse(s));
			} catch (ParseException e1) {
				throw new NumberFormatException(e1.getMessage());
			}
		}
	}
	
	/**  <p>Title: {@link Money#sum}
	 *   <p>Description : Do the sum of a list of Money objects value.
	 *   @param  aMoneys The list of Money objects to sum.
	 *   @return a Money object
	 */
	public static Money sum(Collection<Money> aMoneys){
		Money sum = Money.ZERO;
		for (Money aThat : aMoneys){
			sum = sum.plus(aThat);
		}
		return sum;
	}
	
	/**  <p>Title: {@link Money#compareTo}
	 *   <p>Description : Compares this Money object to the one received in parameter.
	 *   @param  m The Money object to compare
	 *   @return a negative integer, zero, or a positive integer
	 *             as this object is less than, equal to, or greater than
	 *             the object received in parameter.
	 */
	@Override
	public int compareTo(Money m) {
		return value.compareTo(m.value);
	}

	/**  <p>Title: {@link Money#divBy}
	 *   <p>Description : Divide this Money object value by the received parameter.
	 *   @param  divisor : the Number which divides this money object.
	 *   @return a Money object
	 */
	public Money divBy(Number divisor){
		BigDecimal divider = new BigDecimal(divisor.toString());
		return new Money(value.divide(divider, SCALE, roundingMode));
	}

	/**  <p>Title: {@link Money#equals}
	 *   <p>Description : Returns a boolean indicating if this money object is
	 *                    equals to the object received as parameter.<p>
	 *                    The objects are equal if and only if the specified object :
	 *                    <li>is not null,
	 *                    <li>is a Money object, and
	 *                    <li>has the same value as this object.
	 *   @param object : the object that this money object is compared to
	 *   @return <b>true</b> The objects are equal
     *	         <br><b>false</b> The objects are not equal
	 */
    @Override
    public boolean equals(Object object) {
        if (object == this)		// If the object is this object,
        {
            return true;		// the objects are equal by definition.
        }
        if (object == null)		// If the object is null,
        {
            return false;		// the objects are not equal by definition.
        }
        if (!(object instanceof Money))// If the object is not an instance of Money,
        {
            return false;              // the objects are not equal by definition.
        }
        return (this.isEqual(((Money) object))); //If the value of the objects are equal returns true, else false
    }

	/**  <p>Title: {@link Money#isEqual}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is equal to another money value.
     *   @param other : the money object that this object value is compared to
	 *   @return <b>true</b> Monetary values are equal
     *	         <br><b>false</b> Monetary value are not equal
	 */
    public boolean isEqual(Money other){
    	 // Monetary value for comparison
        return (value.compareTo(other.value) == 0);
    }
	
	/**  <p>Title: {@link Money#isGreaterThan}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is greater than another money value.
     *   @param other : the money object that this object value is compared to
	 *   @return <b>true</b> this money value is greater than
	 *                       the money values received as parameter
     *	         <br><b>false</b> this money value is not greater than
     *							  the money values received as parameter
     */

    public boolean isGreaterThan(Money other){
    	return (value.compareTo(other.value) > 0);
    }
	
	/**  <p>Title: {@link Money#isGreaterThanOrEqual}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is greater than or equal to another money value.
     *   @param other : the money object that this object value is compared to
	 *   @return <b>true</b> this money value is greater than or equal to
	 *                       the money values received as parameter
     *	         <br><b>false</b> this money value is not greater than or equal to
     *							  the money values received as parameter
     */
    public boolean isGreaterThanOrEqual(Money other){
        return (value.compareTo(other.value) >= 0);
    }
	
	/**  <p>Title: {@link Money#isLessThan}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is less than another money value.
     *   @param other : the money object that this object value is compared to
	 *   @return <b>true</b> this money value is less than
	 *                       the money values received as parameter
     *	         <br><b>false</b> this money value is not less than
     *							  the money values received as parameter
	 */
    public boolean isLessThan(Money other){
        return (value.compareTo(other.value) < 0);
    }
	
	/**  <p>Title: {@link Money#isLessThanOrEqual}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is less than or equal to another money value.
     *   @param other : the money object that this object value is compared to
	 *   @return <b>true</b> this money value is less than or equal to
	 *                       the money values received as parameter
     *	         <br><b>false</b> this money value is not less than or equal to
     *							  the money values received as parameter
     */
    public boolean isLessThanOrEqual(Money other){
        return (value.compareTo(other.value) <= 0);
    }

	/**  <p>Title: {@link Money#isNegative}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is negative (less than 0.00).
	 *   @return <b>true</b> The monetary value is negative
     *	         <br><b>false</b> The monetary value is not negative
	 */
    public boolean isNegative(){
        return (value.signum() == -1);
    }

	/**  <p>Title: {@link Money#isPositive}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is positive (greater than or equal to 0.00).
	 *   @return <b>true</b> The monetary value is positive
     *	         <br><b>false</b> The monetary value is not positive
	 */
    public boolean isPositive(){
        return (value.signum() >= 0);
    }
	
	/**  <p>Title: {@link Money#isZero}
	 *   <p>Description : Returns a boolean indicating if this money value
     *                    is zero (equal to 0.00).
	 *   @return <b>true</b> The monetary value is zero
     *	         <br><b>false</b> The monetary value is not zero
	 */
	public boolean isZero(){
        return (value.signum() == 0);
    }

	/**  <p>Title: {@link Money#minus(Money)}
	 *   <p>Description : Subtract this Money object value to the one received in parameter.
	 *   @param  aThat The Money object to add
	 *   @return a Money object
	 */
	public Money minus(Money aThat){
		return new Money(value.subtract(aThat.value));
	}

	/**  <p>Title: {@link Money#minus(BigDecimal)}
	 *   <p>Description : Subtract this Money object value to the one received in parameter.
	 *   @param  aThat The BigDecimal object to add
	 *   @return a Money object
	 */
	public Money minus(BigDecimal aThat){
		return new Money(value.subtract(aThat));
	}
    
	/**  <p>Title: {@link Money#minusPercent}
	 *   <p>Description : Subtract to this Money object, the percent
	 *                    received as parameter applied to this object value.
	 *   @param  percent : the Double percent as a decimal number (eg. 0.15 for 15%)
	 *   @return a Money object
	 */
	public Money minusPercent(Double percent){
		return times(1 - percent);
	}

	/**
	 * <p>
	 * Title: {@link Money#negate}
	 * <p>
	 * Description : Returns the negative value of this money object.
	 * 
	 * @return a Money object with -this.value
	 */
	public Money negate() {
		return new Money(value.negate());
	}

	/**  <p>Title: {@link Money#plus}
	 *   <p>Description : Adds this Money object value to the one received in parameter.
	 *   @param  aThat The Money object to add
	 *   @return a Money object
	 */
	public Money plus(Money aThat){
		return new Money(value.add(aThat.value));
	}

	/**  <p>Title: {@link Money#plus}
	 *   <p>Description : Adds this Money object value to the one received in parameter.
	 *   @param  aThat The BigDecimal object to add
	 *   @return a Money object
	 */
	public Money plus(BigDecimal aThat){
		return new Money(value.add(aThat));
	}

	/**  <p>Title: {@link Money#plusPercent}
	 *   <p>Description : Adds to this Money object, the percent
	 *                    received as parameter applied to this object value.
	 *   @param  percent : the Double percent as a decimal number (eg. 0.15 for 15%)
	 *   @return a Money object
	 */
	public Money plusPercent(Double percent){
		return times(1 + percent);
	}

	/**  <p>Title: {@link Money#times}
	 *   <p>Description : Multiply this Money object value by the received parameter.
	 *   @param  factor : the Number factor which multiply this money object.
	 *   @return a Money object
	 */
	public Money times(Number factor){
		BigDecimal multiplier = new BigDecimal(factor.toString());
		return new Money(value.multiply(multiplier));
	}

	/**  <p>Title: {@link Money#doubleValue}
	 *   <p>Description : Return this Money object value as a Double number.
	 *   @return a Double
	 */
	@Override
	public double doubleValue(){
		return value.doubleValue();
	}

	public BigDecimal bigDecimalValue() {
		return value;
	}

	/**  <p>Title: {@link Money#toPlainString}
	 *   <p>Description : Return the string corresponding to the
	 *                    Money value without any formating with
	 *                    all the significant numbers but stripped
	 *                    of any trailing zeros.
	 *   @return this as an unformatted string.
	 */
	public String toPlainString(){
		return this.value.stripTrailingZeros().toPlainString();
	}
	
	/**  <p>Title: {@link Money#toformattedString}
	 *   <p>Description : Return a string formatted with two zero after the
	 *   				  decimal.  The decimal is the one of the system.
	 *                    (0.00 or 0,00).
	 *   @return this formatted with two zero after the decimal.
	 *   @throws IllegalStateException If the money has more than 2 decimal digits round() must be used.
	 */
	public String toFormattedString() throws IllegalStateException {
		validateState();
		return FORMATTER.format(this.value);
	}
	
	/**  <p>Title: {@link Money#toString}
	 *   <p>Description : Return the string corresponding to the
	 *                    Money value formatted with two decimal
	 *                    after the [.].
	 *   @return this as a properly formatted string with a decimal [.].
	 *   @throws IllegalStateException If the money has more than 2 decimal digits round() must be used.
	 */
	@Override
	public String toString() throws IllegalStateException{
		validateState();
		Money m2dig = new Money(this);
		return (m2dig.value.setScale(2, roundingMode)).toString();
	}
	
	/**
	 * Returns the money formatted with the dollar sign, and parentheses if it is negative.
	 * FIXME The dollar sign position should change based on the requested language.
	 * @return
	 * @throws IllegalStateException If the money has more than 2 decimal digits round() must be used.
	 */
	public String toMoneyString() throws IllegalStateException {
		validateState();
		if (isLessThan(ZERO)) {
			return "(" + toString() + ")$";
		} else {
			return toString() + "$";
		}
	}
	
	@Override
	public float floatValue() {
		return value.floatValue();
	}

	@Override
	public int intValue() {
		return value.intValue();
	}

	@Override
	public long longValue() {
		return value.longValue();
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
	
	/**
	 * Returns the number of cents as an int value, here are a few expected results:
	 *   - 123.45  gives: 45
	 *   - 123     gives:  0
	 *   - 123.2   gives: 20
	 *   - 123.451 gives: 45
	 *   - 123.455 gives: 46
	 * @return
	 */
	public int getCents() {
//		String s = value.toString();
//		return Integer.parseInt(value.toString().substring(s.length()-2));
		return Math.round(value.subtract(new BigDecimal(value.toBigInteger())).movePointRight(2).floatValue());
	}
	
	/**
	 * Returns the absolute value of this Money, similar to {@link Math#abs(double)}.
	 * @return
	 */
	public Money abs() {
		return isGreaterThanOrEqual(ZERO)? this: this.times(-1);
	}
	
	/**
	 * Returns a new rounded Money with 2 digits.
	 * @return
	 */
	public Money round() {
		return new Money(value.setScale(2, roundingMode));
	}
	
	/**
	 * Throws an IllegalStateException if this Money is in an illegal state, i.e. if it
	 * has more than 2 digits and therefore would require a round() before printing or saving.
	 * @throws IllegalStateException
	 */
	public void validateState() throws IllegalStateException {
		if (value.scale() > 2) {
			throw new IllegalStateException("This Money has more than 2 decimal digits, I can't print it nor store it: " + value);
		}
	}
}
