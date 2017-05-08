package main.kotlin.kgp.fitness

import com.opencsv.CSVReader
import java.io.FileReader

interface CaseLoader {
    fun loadCases(): Cases
}

data class CsvCaseLoaderOptions(val filename: String, val numFeatures: Int)

class CsvCaseLoader(val options: CsvCaseLoaderOptions) : CaseLoader {

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