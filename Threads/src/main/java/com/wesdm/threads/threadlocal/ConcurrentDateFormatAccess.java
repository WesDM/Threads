package com.wesdm.threads.threadlocal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Thread safe date formatter.  Thread local approach provides the best performance.
 * @author Wesley
 *
 */
public class ConcurrentDateFormatAccess {
	private ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {

		@Override
		public DateFormat get() {
			return super.get();
		}

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy MM dd");
		}

		@Override
		public void remove() {
			super.remove();
		}

		@Override
		public void set(DateFormat value) {
			super.set(value);
		}

	};

	public Date convertStringToDate(String dateString) throws ParseException {
		return df.get().parse(dateString);
	}
}
