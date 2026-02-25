package com.zybooks.mobile2app;

public final class SparePartContract {
    
    private SparePartContract () {
        //hidden constructor, do nothing
    }
    
    public static class SparePartEntry {

        public static final String TABLE_NAME = "spare_parts";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_OEM_NUMBER = "oem_number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_QUANTITY = "quantity";

    }
    
}
