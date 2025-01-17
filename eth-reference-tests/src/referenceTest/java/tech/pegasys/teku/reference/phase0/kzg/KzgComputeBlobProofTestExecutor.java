/*
 * Copyright Consensys Software Inc., 2022
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.reference.phase0.kzg;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.tuweni.bytes.Bytes;
import tech.pegasys.teku.ethtests.finder.TestDefinition;
import tech.pegasys.teku.kzg.KZGCommitment;
import tech.pegasys.teku.kzg.KZGProof;
import tech.pegasys.teku.kzg.ckzg4844.CKZG4844;

public class KzgComputeBlobProofTestExecutor extends KzgTestExecutor {

  @Override
  public void runTestImpl(final TestDefinition testDefinition) throws Throwable {
    final Data data = loadDataFile(testDefinition, Data.class);
    final KZGProof expectedKzgProof = data.getOutput();
    KZGProof actualKzgProof;
    try {
      final Bytes blob = data.getInput().getBlob();
      final KZGCommitment commitment = data.getInput().getCommitment();
      actualKzgProof = CKZG4844.getInstance().computeBlobKzgProof(blob, commitment);
    } catch (RuntimeException e) {
      actualKzgProof = null;
    }
    assertThat(actualKzgProof).isEqualTo(expectedKzgProof);
  }

  private static class Data {
    @JsonProperty(value = "input", required = true)
    private Input input;

    @JsonProperty(value = "output", required = true)
    private String output;

    public Input getInput() {
      return input;
    }

    public KZGProof getOutput() {
      return output == null ? null : KZGProof.fromHexString(output);
    }

    private static class Input {
      @JsonProperty(value = "blob", required = true)
      private String blob;

      @JsonProperty(value = "commitment", required = true)
      private String commitment;

      public Bytes getBlob() {
        return Bytes.fromHexString(blob);
      }

      public KZGCommitment getCommitment() {
        return KZGCommitment.fromHexString(commitment);
      }
    }
  }
}
