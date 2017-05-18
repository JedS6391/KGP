package main.kotlin.kgp.fitness

import com.opencsv.CSVReader
import java.io.FileReader

/**
 * A component that can load a set of cases from some source.
 */
interface CaseLoader {

    /**
     * Loads a set of cases.
     *
     * @return A set of [Case]s.
     */
    fun loadCases(): Cases
}

/**
 * Options to configure a [CsvCaseLoader] instance.
 *
 * @property filename The name of the CSV file to load the cases from.
 * @property numFeatures The number of features each case has.
 */
data class CsvCaseLoaderOptions(val filename: String, val numFeatures: Int)

/**
 * A [CaseLoader] that loads cases from a CSV file.
 *
 * @property options A set of options to configure this loader.
 */
class CsvCaseLoader(val options: CsvCaseLoaderOptions) : CaseLoader {

    /**
     * Loads a set of cases from the CSV file specified in the options.
     *
     * Each case will have [CsvCaseLoaderOptions.numFeatures] features and is expected
     * to have a single output value.
     *
     * Assumes that the CSV file has a header in the first row.
     *
     * @return A set of cases, where each case has the shape [numFeatures + output].
     */
    override fun loadCases(): Cases {
        val reader = CSVReader(FileReader(this.options.filename))
        val lines = reader.readAll()

        // Assumes header is in the first row
        val header = lines.removeAt(0)

        return lines.map { line ->
            val features = line.take(this.options.numFeatures).mapIndexed { idx, raw ->
                Feature(
                    value = raw.toDouble(),
                    name = header[idx]
                )
            }

            val output = line.last().toDouble()

            Case(features, output)
        }
    }
}