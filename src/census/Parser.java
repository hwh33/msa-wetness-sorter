package census;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * A parser designed to read U.S. Census data stored in a (pre-2007) Excel file (.xls).
 * The MSA data should begin immediately following a row in which the first cell is a column
 * header reading "Metropolitan Statistical Area".
 * Each row after this should represent a single MSA. The first cell should give the central urban
 * area of the MSA. The third cell should give the current population of the MSA (as recorded at
 * the time of the census). The fourth cell should give the MSA's population change over the
 * decade preceding the census (so from the previous census).
 * Constant variables pertaining to these format specifications are defined at the beginning of a
 * private class called MSAIterator, defined in this file.
 * 
 * @author Harry Harpham
 */
public class Parser {
	
	/**
	 * Parses U.S. Census data in the file located at the input file path to return an iterator of
	 * MetropolitanStatisticalArea objects representing each MSA in the file.
	 * TODO: identify assumed format of file
	 * 
	 * @param censusDataFilepath	the path to the file containing the census data; this file
	 * 								should be a pre-2007 Excel file (.xls)
	 * @param statsTimeStamp		the date at which the population statistics in the given file
	 * 								were accurate
	 * @return						an iterator over MetropolitanStatisticalArea objects
	 * 								representing each of the MSAs listed in the input file
	 * @throws IOException			if there is a problem locating or loading the file at 
	 * 								censusDataFilepath
	 */
	public static Iterator<MetropolitanStatisticalArea> getMSAIterator(String censusDataFilepath, 
			LocalDateTime statsTimeStamp) throws IOException {
		// We want data from the first sheet (there should be only one anyway)
		final int MSA_DATA_SHEET_NUMBER = 0;
		
		final String LAST_COL_HEADER_PRE_MSA_LIST = "Metropolitan statistical area";
				
		// Check that we've been given a file of the proper format.
		if (!censusDataFilepath.endsWith(".xls")) {
			throw new IllegalArgumentException();
		}
		
		FileInputStream inputStream = new FileInputStream(new File(censusDataFilepath));
		HSSFWorkbook censusDataWorkbook = new HSSFWorkbook(inputStream);
		HSSFSheet censusDataSheet = censusDataWorkbook.getSheetAt(MSA_DATA_SHEET_NUMBER);
		
		// This is set to true when we expect the next row to contain MSA data (as opposed to
		// a row header).
		boolean readingMSAData = false;
		
		// We move down the table row by row until we find the beginning of the MSA data.
		int currentRowIndex = 0;
		while (!readingMSAData) {
			HSSFRow currentRow = censusDataSheet.getRow(currentRowIndex);
			HSSFCell firstCell = currentRow.getCell(0);
			
			if(firstCell.getCellType() == HSSFCell.CELL_TYPE_STRING &&
					firstCell.getStringCellValue() == LAST_COL_HEADER_PRE_MSA_LIST) {
				// If the current row is the last row before the MSA list, then we will begin
				// reading MSA data on the next row.
				readingMSAData = true;
			}
			currentRowIndex++;
		}
		
		Iterator<MetropolitanStatisticalArea> newMSAIterator = new MSAIterator(currentRowIndex,
				censusDataSheet, statsTimeStamp);
		
		censusDataWorkbook.close();
		
		return newMSAIterator;
	}

}

/*
 * We define a private class MSAIterator to implement an iterator over MetropolitanStatisticalArea
 * objects. This way we can navigate to the appropriate location in the file, then create our 
 * iterator using local variables passed to the MSAIterator constructor.
 */
class MSAIterator implements Iterator<MetropolitanStatisticalArea> {
	final int CENTRAL_URBAN_AREA_COL_INDEX = 0;
	final int MOST_RECENT_POP_COL_INDEX = 2;
	final int POP_CHANGE_COL_INDEX = 3;
	
	final int TOTAL_YEARS_FOR_POP_CHANGE = 10;
	
	private int currentRowIndex;
	private HSSFSheet censusDataSheet;
	private LocalDateTime statsTimeStamp;
	
	public MSAIterator(int startRowIndex, HSSFSheet censusDataSheet, LocalDateTime statsTimeStamp) {
		this.currentRowIndex = startRowIndex;
		this.censusDataSheet = censusDataSheet;
		this.statsTimeStamp = statsTimeStamp;
	}
	
	public boolean hasNext() {
		// The MSA list is followed by a blank line (after which begins the list of
		// micropolitan statistical areas), so we finish when we find a blank cell in the
		// first column.
		HSSFRow nextRow = censusDataSheet.getRow(currentRowIndex+1);
		HSSFCell firstCellNextRow = nextRow.getCell(0);
		return (firstCellNextRow.getCellType() == HSSFCell.CELL_TYPE_STRING &&
				firstCellNextRow.getStringCellValue() == "");
	}
	
	public MetropolitanStatisticalArea next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}
		
		HSSFRow currentRow = censusDataSheet.getRow(currentRowIndex++);
		HSSFCell centralUrbanAreaCell = currentRow.getCell(CENTRAL_URBAN_AREA_COL_INDEX);
		HSSFCell mostRecentPopCell = currentRow.getCell(MOST_RECENT_POP_COL_INDEX);
		HSSFCell popChangeCell = currentRow.getCell(POP_CHANGE_COL_INDEX);
		
		if (centralUrbanAreaCell.getCellType() != HSSFCell.CELL_TYPE_STRING ||
				mostRecentPopCell.getCellType() != HSSFCell.CELL_TYPE_NUMERIC ||
				popChangeCell.getCellType() != HSSFCell.CELL_TYPE_NUMERIC) {
			throw new IllegalArgumentException();
		}
		
		String centralUrbanArea = centralUrbanAreaCell.getStringCellValue();
		// The value in mostRecentPopCell should be an integer and much less than the
		// maximum int value; we should have no loss of information in casting to int.
		int censusPopulation = (int) mostRecentPopCell.getNumericCellValue();
		// The value in popChangeCell should be an integer and much less than the maximum
		// int value; we should have no loss of information in casting the result to float.
		float averageYearlyPopChange = (float) (
				popChangeCell.getNumericCellValue() / TOTAL_YEARS_FOR_POP_CHANGE);
		
		return new MetropolitanStatisticalArea(centralUrbanArea, statsTimeStamp,
				censusPopulation, averageYearlyPopChange);
	}
}
