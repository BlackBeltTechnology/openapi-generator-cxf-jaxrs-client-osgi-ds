/*-
 * #%L
 * Blackbelt :: OpeanAPI :: Codegen :: CXF JaxRS OSGi service client
 * %%
 * Copyright (C) 2018 - 2023 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

// a wrapper closure around executing a string
// can take either a string or a list of strings (for arguments with spaces)
// prints all output, complains and halts on error
def runCommand = { strList ->
  assert ( strList instanceof String ||
    ( strList instanceof List && strList.each{ it instanceof String } ) \
)
  def proc = strList.execute(null, new File("target/it/k8s/generated"))
  proc.in.eachLine { line -> println line }
  proc.out.close()
  proc.waitFor()

  print "[INFO] ( "
  if(strList instanceof List) {
    strList.each { print "${it} " }
  } else {
    print strList
  }
  println " )"

  if (proc.exitValue()) {
    println "gave the following error: "
    println "[ERROR] ${proc.getErrorStream()}"
  }
  assert !proc.exitValue()
}

runCommand("mvn clean test")
