import Link from "next/link";
import { Activity, Clock, Server, PlayCircle } from "lucide-react";

export default function Dashboard() {
  return (
    <div className="max-w-5xl mx-auto py-8">
      <header className="mb-10">
        <h1 className="text-3xl font-bold text-white mb-2 tracking-tight">Dashboard</h1>
        <p className="text-slate-400">Welcome to Chronos Temporal Execution Engine.</p>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 font-medium">Active Environments</h3>
            <Server size={18} className="text-indigo-400" />
          </div>
          <p className="text-3xl font-bold text-white">1</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 font-medium">Total Timelines</h3>
            <Clock size={18} className="text-cyan-400" />
          </div>
          <p className="text-3xl font-bold text-white">3</p>
        </div>
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-slate-400 font-medium">Recorded Events</h3>
            <Activity size={18} className="text-emerald-400" />
          </div>
          <p className="text-3xl font-bold text-white">1,248</p>
        </div>
      </div>

      <div>
        <h2 className="text-xl font-bold text-white mb-6">Recent Timelines</h2>
        <div className="space-y-4">
          <Link href="/timeline/demo">
            <div className="bg-slate-900/80 border border-slate-800 hover:border-indigo-500/50 transition-colors rounded-xl p-5 flex items-center justify-between group cursor-pointer">
              <div>
                <h3 className="text-lg font-semibold text-white mb-1 group-hover:text-indigo-400 transition-colors">E-Commerce Demo</h3>
                <p className="text-sm text-slate-400">Main execution timeline for the online shopping system.</p>
              </div>
              <div className="flex items-center space-x-4">
                <span className="px-3 py-1 bg-emerald-500/10 text-emerald-400 rounded-full text-xs font-medium border border-emerald-500/20">Active</span>
                <PlayCircle size={24} className="text-slate-600 group-hover:text-indigo-400 transition-colors" />
              </div>
            </div>
          </Link>

          <Link href="/timeline/exp-001">
            <div className="bg-slate-900/80 border border-slate-800 hover:border-indigo-500/50 transition-colors rounded-xl p-5 flex items-center justify-between group cursor-pointer">
              <div>
                <h3 className="text-lg font-semibold text-white mb-1 group-hover:text-indigo-400 transition-colors">Payment Failure Experiment (EXP-001)</h3>
                <p className="text-sm text-slate-400">Forked from E-Commerce Demo at Event #102</p>
              </div>
              <div className="flex items-center space-x-4">
                <span className="px-3 py-1 bg-amber-500/10 text-amber-400 rounded-full text-xs font-medium border border-amber-500/20">Experiment</span>
                <PlayCircle size={24} className="text-slate-600 group-hover:text-indigo-400 transition-colors" />
              </div>
            </div>
          </Link>
        </div>
      </div>
    </div>
  );
}
