"use client";

import { useEffect, useState } from "react";
import { AlertTriangle, Clock, Activity, CheckCircle, XCircle, FileText, Zap, ShieldAlert, AlertCircle } from "lucide-react";

export default function IncidentReportPage({ params }: { params: { id: string } }) {
  const [report, setReport] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // In a real app, we would fetch from the API:
    // fetch(`/api/incidents/analyze?eventId=${params.id}`)
    // For this demonstration, we'll use a timeout to simulate API loading
    // and provide mock data aligned with Phase 10 requirements.
    
    setTimeout(() => {
      setReport({
        incidentId: "INC-" + params.id.substring(0, 8),
        failedEventId: params.id,
        outcome: "ORDER_FAILED",
        rootCauseAttribution: "Root Cause Candidate",
        faultAnalysis: ["LATENCY-01"],
        traceImpact: "+3000 ms causal delay",
        causalChain: [
          "ORDER_CREATED",
          "PAYMENT_STARTED",
          "PAYMENT_SUCCESS",
          "INVENTORY_TIMEOUT",
          "ORDER_FAILED"
        ],
        counterfactualProof: {
          status: "PROVEN", // or NOT_AVAILABLE, AMBIGUOUS
          removedFaults: ["LATENCY-01"],
          whatIfOutcome: "ORDER_COMPLETED",
          realDurationMs: 3500,
          whatIfDurationMs: 500
        },
        evidenceMetrics: {
          totalEvents: 7,
          totalFaults: 1,
          causalEdges: 6
        },
        evidenceChain: [
            { evidenceId: "E1", description: "PAYMENT_STARTED" },
            { evidenceId: "E2", description: "FAULT_TRIGGERED (LATENCY-01)" },
            { evidenceId: "E3", description: "PAYMENT_SUCCESS" },
            { evidenceId: "E4", description: "INVENTORY_TIMEOUT" },
            { evidenceId: "E5", description: "ORDER_FAILED" },
            { evidenceId: "E6", description: "EXP-002: LATENCY-01 removed" },
            { evidenceId: "E7", description: "INVENTORY_RESERVED" },
            { evidenceId: "E8", description: "ORDER_COMPLETED" }
        ]
      });
      setLoading(false);
    }, 1000);
  }, [params.id]);

  if (loading) {
    return <div className="flex h-full items-center justify-center text-slate-400">Loading Incident Analysis...</div>;
  }

  return (
    <div className="max-w-4xl mx-auto py-10 w-full">
      <div className="mb-8">
        <h1 className="text-3xl font-black text-white flex items-center">
          <ShieldAlert className="mr-3 text-rose-500" size={32} />
          {report.incidentId}
          <span className="ml-4 px-3 py-1 bg-indigo-500/10 text-indigo-400 rounded-md text-xs font-bold border border-indigo-500/20 uppercase">
            Phase 10
          </span>
        </h1>
        <h2 className="text-xl text-rose-400 font-mono mt-2">{report.outcome}</h2>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 col-span-1 md:col-span-1">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-4">Root Cause</h3>
          <div className="text-2xl font-bold text-white mb-1">
            {report.faultAnalysis && report.faultAnalysis.length > 0 ? report.faultAnalysis[0] : "Unknown"}
          </div>
          <div className="text-sm text-rose-400 font-mono">{report.traceImpact}</div>
          <div className="mt-4 inline-block px-2 py-1 bg-amber-500/10 border border-amber-500/20 text-amber-500 text-[10px] rounded uppercase font-bold">
            {report.rootCauseAttribution}
          </div>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 col-span-1 md:col-span-2">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-4">Counterfactual Proof</h3>
          
          {report.counterfactualProof.status === "PROVEN" ? (
            <div className="grid grid-cols-2 gap-4">
              <div className="bg-slate-950 p-4 rounded-lg border border-rose-500/20">
                <div className="text-slate-500 text-xs uppercase font-bold mb-2">Real</div>
                <div className="text-rose-400 font-bold mb-1 flex items-center">
                  <XCircle size={14} className="mr-1.5" /> FAILED
                </div>
                <div className="text-slate-400 font-mono text-sm">{report.counterfactualProof.realDurationMs} ms</div>
              </div>
              <div className="bg-slate-950 p-4 rounded-lg border border-emerald-500/20">
                <div className="text-slate-500 text-xs uppercase font-bold mb-2">What-If</div>
                <div className="text-emerald-400 font-bold mb-1 flex items-center">
                  <CheckCircle size={14} className="mr-1.5" /> {report.counterfactualProof.whatIfOutcome}
                </div>
                <div className="text-slate-400 font-mono text-sm">{report.counterfactualProof.whatIfDurationMs} ms</div>
              </div>
              <div className="col-span-2 mt-2 flex items-center text-sm text-slate-300">
                <Zap size={14} className="text-indigo-400 mr-2" />
                Fault removed: <span className="font-mono text-indigo-300 ml-2">{report.counterfactualProof.removedFaults?.join(", ")}</span>
              </div>
            </div>
          ) : report.counterfactualProof.status === "AMBIGUOUS" ? (
            <div className="flex flex-col items-center justify-center h-full text-amber-500">
              <AlertTriangle size={24} className="mb-2" />
              <p className="font-bold text-sm">Ambiguous Experiments</p>
              <p className="text-xs text-slate-400 mt-1">Multiple counterfactuals match this fault.</p>
            </div>
          ) : (
            <div className="flex flex-col items-center justify-center h-full text-slate-500">
              <AlertCircle size={24} className="mb-2" />
              <p className="font-bold text-sm">Not Available</p>
              <p className="text-xs text-slate-600 mt-1">No counterfactual experiment run for this incident.</p>
            </div>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-6">Causal Chain</h3>
          <ul className="space-y-4 relative before:absolute before:inset-y-0 before:left-[11px] before:w-0.5 before:bg-slate-800">
            {report.causalChain.map((evt: string, i: number) => (
              <li key={i} className="flex items-center relative z-10 pl-8">
                <div className={`absolute left-1 w-2.5 h-2.5 rounded-full ring-4 ring-slate-900 ${i === report.causalChain.length - 1 ? "bg-rose-500 ring-rose-500/20" : "bg-slate-600"}`}></div>
                <span className={`font-mono text-sm ${i === report.causalChain.length - 1 ? "text-rose-400 font-bold" : "text-slate-300"}`}>
                  {evt}
                </span>
              </li>
            ))}
          </ul>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-xl p-6 flex flex-col">
          <h3 className="text-slate-500 text-xs font-bold uppercase tracking-wider mb-6">Evidence</h3>
          
          <div className="flex space-x-6 mb-6 pb-6 border-b border-slate-800">
            <div>
              <div className="text-2xl font-bold text-white">{report.evidenceMetrics?.totalEvents || 0}</div>
              <div className="text-[10px] text-slate-500 uppercase font-bold">Events</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-white">{report.evidenceMetrics?.totalFaults || 0}</div>
              <div className="text-[10px] text-slate-500 uppercase font-bold">Faults</div>
            </div>
            <div>
              <div className="text-2xl font-bold text-white">{report.evidenceMetrics?.causalEdges || 0}</div>
              <div className="text-[10px] text-slate-500 uppercase font-bold">Causal Edges</div>
            </div>
          </div>

          <div className="flex-1 overflow-y-auto pr-2">
            <h4 className="text-[10px] text-slate-500 uppercase font-bold mb-3">Evidence Chain</h4>
            <div className="space-y-2">
              {report.evidenceChain.map((ev: any, i: number) => (
                <div key={i} className="bg-slate-950 p-2.5 rounded border border-slate-800 flex items-start">
                  <div className="text-xs font-bold text-slate-500 mr-3 w-6">{ev.evidenceId}</div>
                  <div className="text-xs font-mono text-slate-300">{ev.description}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
